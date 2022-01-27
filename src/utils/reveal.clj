(ns reveal
  (:require
    [vlaaad.reveal :as r]
    [vlaaad.reveal.ext :as rx]
    [vlaaad.reveal.prefs :as rp]
    [tdebug :refer [trace> trace>>]]))
    ;[integrant.repl.state :refer [system config preparer]]))
;[app.readers :refer [readers]]
;[com.verybigthings.funicular.core]))

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
             #(rx/stream-as-is
                (rx/horizontal
                  (rx/raw-string "=>" {:fill :util})
                  rx/separator
                  (rx/stream %))))))

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

;(defn system-rui []
;  (add-frozen-rui
;    (-> system
;      (dissoc :duct.migrator.ragtime/resources)
;      (dissoc :duct.migrator/ragtime))
;    system-))
;
;(defn funicular-rui []
;  (add-frozen-rui
;    (->
;      (private-field
;        (-> system
;          :app/funicular)
;        "compiled")
;      (trace> ::funicular))
;    funicular-))

;(defn default-ruis
;  "Opens three Reveal windows (RUI):
;  'Playground' - empty and accepts all further TAPs
;  'System' - shows project's System VAR, don't accept new TAPs
;  'Funicular' - shows Funicular object from the System, don't accept new TAPs"
;  []
;  (system-rui)
;  (Thread/sleep 200)
;  (funicular-rui)
;  (Thread/sleep 200)
;  (add-tap-rui))

(comment
  (set-prefs {:font-size 12, :theme :dark})
  ;(trace>> ::system (-> system
  ;                    (dissoc :duct.migrator.ragtime/resources)
  ;                    (dissoc :duct.migrator/ragtime)))
  ;(-> system
  ;  :app/funicular
  ;  (trace> ::funicular))




  ;_________________________________________________________________________________
  ; reify example

  (defprotocol shape
    "A geometric shape."
    (area [this dummy]))

  (defn make-circle
    [{:keys [radius dummy-1] :as api}]
    (let [radius- (+ radius 0)]
      (reify shape
        (area [_ dummy]
          (+ (* Math/PI radius- radius-) dummy)))))

  (def circle (make-circle {:radius 8 :dummy-1 0}))

  (tap> circle)
  (tap> (. circle area 0))

  ;_________________________________________________________________________________
  ; set Reveal prefs, send command




  (tap> {:vlaaad.reveal/command '(open-view {:fx/type action-view
                                             :action  :vlaaad.reveal.action/view:table
                                             :value   v})
         :env                   {'v (ns-publics *ns*)}})

  (tap> (r/sticker
          {:fx/type r/ref-watch-latest-view
           :ref     #'system}
          :title "integrant system")))
