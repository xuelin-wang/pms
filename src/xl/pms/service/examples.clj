(ns xl.pms.service.examples)

(defn handle-hello [{:keys [request] :as context}]
  (let [{:keys [path-params]} request]
    (assoc context :response {:body (str "Hello, " (:user-id path-params)) :status 200})
    )
  )

(def hello-interceptor
  {
   :name ::hello-user
   :enter handle-hello
   }
  )

