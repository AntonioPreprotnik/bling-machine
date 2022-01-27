(ns todo-app.interceptors
  (:require
    [xiana.core :as xiana]))

(def sample-todo-app-controller-interceptor
  {:enter (fn [{request :request {:keys [handler controller match]} :request-data :as state}]
            (xiana/ok state))
   :leave (fn [{response :response :as state}]
            (xiana/ok state))})
