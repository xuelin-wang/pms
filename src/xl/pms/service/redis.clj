(ns xl.pms.service.redis
  (:require [xl.db.redis :as r])
  )

(defn- retval-json [val]
  (str "{\"result\":\"" val "\"}" ))

(defn- set-response [body context]
  (assoc context :response {:body body :status 200})
  )

(defn- to-str-arr [val]
  (into-array String (if (vector? val) val [val]))
  )

(defn create-handle-redis [redis-pool]
  (fn [{:keys [request] :as context}]
    (let [{:keys [path-params query-params]} request
          cmd (:cmd path-params)]
      (case cmd
        "exists"
        (let [{:keys [k]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.exists redis k)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "get"
        (let [{:keys [k]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.get redis k)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "lpop"
        (let [{:keys [k]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.lpop redis k)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "lpush"
        (let [{:keys [k v]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.lpush redis k (to-str-arr v))
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "lrange"
        (let [{:keys [k start end]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.lrange redis k (Long/parseLong start) (Long/parseLong end))
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "lset"
        (let [{:keys [k index v]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.lset redis k (Long/parseLong index) v)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "ltrim"
        (let [{:keys [k start end]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.ltrim redis k (Long/parseLong start) (Long/parseLong end))
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "rpop"
        (let [{:keys [k]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.rpop redis k)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "rpush"
        (let [{:keys [k v]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.rpush redis k (to-str-arr v))
                                     )
              ]
          (set-response (retval-json val) context)
          )
        "set"
        (let [{:keys [k v]} query-params
              val (r/with-redis-pool redis-pool redis
                                     (.set redis k v)
                                     )
              ]
          (set-response (retval-json val) context)
          )
        (set-response
          (str "{\"error\":\"invalid redis command: " cmd "," request "\"}" ) context)
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
