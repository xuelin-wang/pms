(ns xl.db.redis
  (:import (redis.clients.jedis JedisPool JedisPoolConfig)))


(defn create-jedis-pool [host port]
  (JedisPool. (JedisPoolConfig.) host port)
  )

(defmacro with-redis-pool [pool redis & body]
  `(with-open [~redis (.getResource ~pool)]
     ~@body
     )
  )

(defn get-rows [pool key-pattern]
  (with-redis-pool pool redis
                   (let [rkeys (.keys redis key-pattern)
                         ]
                     (map (fn [rkey]
                            (let [rval (.get redis rkey)]
                              [rkey rval]
                              )
                            )
                          rkeys
                          )
                     )
                   )
  )


(defn get-list-rows [pool key-pattern]
  (with-redis-pool pool redis
                   (let [rkeys (.keys redis key-pattern)
                         ]
                     (map (fn [rkey]
                            (let [rval (.lrange redis rkey 0 -1)]
                              [rkey rval]
                              )
                            )
                          rkeys
                          )
                     )
                   )
  )
