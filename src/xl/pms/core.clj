(ns xl.pms.core
  (:require
    [xl.pms.server]
    [integrant.core :as ig]
    )
  (:gen-class)
  )

(defn -main [& args]
  (ig/init (xl.pms.server/get-conf "prod")))
