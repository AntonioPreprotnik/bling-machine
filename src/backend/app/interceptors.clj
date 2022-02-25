(ns app.interceptors
  (:require
   [xiana.core :as xiana]))

(def sample-app-controller-interceptor
  {:enter (fn [state]
            (xiana/ok state))
   :leave (fn [state]
            (xiana/ok state))})
