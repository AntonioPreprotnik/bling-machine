(ns reveal
  (:require
   [vlaaad.reveal :as r]
   [vlaaad.reveal.stream :as stream]
   [vlaaad.reveal.prefs :as rp]))

(defn private-field [obj fn-name-string]
  (let [m (.. obj getClass (getDeclaredField fn-name-string))]
    (. m (setAccessible true))
    (. m (get obj))))

(defn set-prefs
  "`(set-prefs {...})` to reset Reveal prefs without restarting your repl"
  [prefs]
  (tap>
   (do (alter-var-root #'rp/prefs (constantly (delay prefs)))
       (require '[vlaaad.reveal.style]
                '[vlaaad.reveal.font] :reload))))

(defn add-tap-default-rui
  "Util used to display prompt before new TAP in Reveal window (RUI)"
  [rui]
  (add-tap (comp rui
                 #(stream/as-is
                   (stream/horizontal
                    (stream/raw-string "=>" {:fill :util})
                    stream/separator
                    (stream/stream %))))))

(defmacro add-tap-rui
  "Without any arguments displays the default Reveal window with prompt and title 'Playground'. Don't accepts `remove-tap`.
   If it has the arguments, the first one is used as the window title. No prompt. Can be 'frozen' with `(remove-tap 'title')`
   The title argument should be a symbol, not a string."
  [& rui-name]
  (let [title (first rui-name)
        rui (or title 'playground)]
    `(do (def ~rui (r/ui {:title (name '~rui)}))
         (if ~title
           (add-tap ~rui)
           (add-tap-default-rui ~rui)))))

(defmacro add-frozen-rui
  "Displays a RUI with title, sends data to it, and 'froze' it with `remove-tap` immediately"
  [data title]
  `(do (add-tap-rui ~title)
       (tap> ~data)
       (println (str "r/ui " ~title " started"))
       (remove-tap ~title)))

(defmacro snapshot-rui
  "Open text files form location 'folder/rui-name' and show it in 'frozen window'.
   Those text files are generated using `(spit *v)` or `(spit (fn  *v))` from some Reveal window"
  [folder rui-name]
  (let [data (slurp (str (name folder) "/" (name rui-name)))]
    `(binding [*default-data-reader-fn* tagged-literal]
       (add-frozen-rui (read-string ~data) ~rui-name))))

(defmacro open-snapshot
  "With two arguments just calls the 'snapshot-rui'
   The third argument is used to add/modify comments to data saved in the text files.
   Comment can be anything form string to the data structure. Shows the file in Reveal window with the new comment.
   'folder' and 'rui-name' should be the symbols"
  ([folder rui-name] `(snapshot-rui ~folder ~rui-name))
  ([folder rui-name comment]
   (binding [*default-data-reader-fn* tagged-literal]
     (when comment
       (let [data (read-string (slurp (str (name folder) "/" (name rui-name))))
             remove-comment (if (:comment data) (dissoc data :comment) data)
             with-comment {:comment comment
                           :data    (or (:data remove-comment) remove-comment)}]
         (spit (str (name folder) "/" (name rui-name)) with-comment)))
     `(snapshot-rui ~folder ~rui-name))))

(defn change-font [size]
  (set-prefs {:font-size size, :theme :dark}))

(comment
  (set-prefs {:font-size 12, :theme :dark})

  (tap> {:vlaaad.reveal/command '(open-view {:fx/type action-view
                                             :action  :vlaaad.reveal.action/view:table
                                             :value   v})
         :env                   {'v (ns-publics *ns*)}})

  (tap> (r/sticker
         {:fx/type r/ref-watch-latest-view}
         :title "integrant system")))
