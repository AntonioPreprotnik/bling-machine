(ns lsp-clean-ns
  (:require
   [babashka.pods :as pods]))

(pods/load-pod 'com.github.clojure-lsp/clojure-lsp "2022.02.01-20.02.32")

(require '[clojure-lsp.api :as api])

(let [args *command-line-args*
      res (if (= (first args) "dry")
            (dissoc (api/clean-ns! {:settings {:clean {:ns-inner-blocks-indentation   :next-line
                                                       :ns-import-classes-indentation :next-line}}
                                    :dry?     true}) :edits)
            (dissoc (api/clean-ns! {:settings {:clean {:ns-inner-blocks-indentation   :next-line
                                                       :ns-import-classes-indentation :next-line}}})
                    :edits))]
  (System/exit (:result-code res)))
