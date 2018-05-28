(ns xl.pms.service.redis
  (:require [xl.db.redis :as r])
  )

(defn create-handle-redis [redis-pool]
  (fn [{:keys [request] :as context}]
    (let [{:keys [path-params query-params]} request
          cmd (:cmd path-params)]
      (case cmd
        "get"
        (let [{:keys [k]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.get redis k)
                                     )
              ]
          (assoc context :response {:body (str "{\"value\":\"" val "\"}" ) :status 200})
          )
        "set"
        (let [{:keys [k v]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.set redis k v)
                                     )
              ]
          (assoc context :response {:body (str "{\"success\":true}" ) :status 200})
          )
        (assoc context :response {:body (str "{\"error\":\"invalid redis command: " cmd "," request "\"}" ) :status 200})
        )
      )
    )
  )


(defn create-redis-interceptor [redis-pool]
  {
   :name ::redis
   :enter (create-handle-redis redis-pool)
   }
  )
