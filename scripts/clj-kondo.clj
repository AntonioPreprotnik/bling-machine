(require '[babashka.pods :as pods])

(pods/load-pod 'clj-kondo/clj-kondo "2022.02.09")

(require '[pod.borkdude.clj-kondo :as clj-kondo])

(let [res (clj-kondo/run! {:lint     ["src" "test" "dev"]
                           :parallel true})]
  (clj-kondo/print! res)

  (if-not (empty? (:findings res))
      (System/exit 2)))



