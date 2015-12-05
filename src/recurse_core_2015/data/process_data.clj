(ns recurse-core-2015.data.process-data
  (:require [cheshire.core :as cheshire]
            [clojure.string :refer [join]]))

(defmacro defdata [name path]
  `(def ~name (cheshire/parse-stream (clojure.java.io/reader ~path))))

(defn get-column-names [table]
  (as-> table $
    (get-in $ ["meta" "view" "columns"])
    (map #(get % "name") $)))

(defn filter-columns [table required-columns]
  (let [column-names (get-column-names table)
        column-idxs  (map #(.indexOf column-names %) required-columns)]
    (sequence (map #(map (partial nth %) column-idxs))
              (get table "data"))))

(defdata raw-restaurants "resources/DOHMH_Restaurant.json")
(def restaurants-columns ["Incident Address" "Incident Zip" "Latitude" "Longitude"])
(def restaurants (map (partial zipmap [:address :zip :latitude :longitude])
                      (set (filter-columns raw-restaurants restaurants-columns))))

(defdata raw-complaints "resources/dohmh_complaints.json")
(def complaints-columns ["DBA" "CUISINE DESCRIPTION" "STREET" "BUILDING" "ZIPCODE"])
(def complaints (map #(hash-map :title (nth % 0)
                                :cuisine (nth % 1)
                                :address (join " " (map (partial nth %) [2 3]))
                                :zip (nth % 4))
                     (set (filter-columns raw-complaints complaints-columns))))
