(ns lsp-format
  (:require
   [babashka.pods :as pods]))

(pods/load-pod 'com.github.clojure-lsp/clojure-lsp "2022.02.01-20.02.32")

(require '[clojure-lsp.api :as api])

(let [args *command-line-args*
      res (if (= (first args) "dry")
            (dissoc  (api/format! {:dry? true}) :edits)
            (dissoc  (api/format! {}) :edits))]
  (System/exit (:result-code res)))
