(ns pms.core
  (:require
    [pms.server]
    [integrant.core :as ig]
    )
  (:gen-class)
  )

(defn -main [& args]
  (ig/init pms.server/sysconf))
