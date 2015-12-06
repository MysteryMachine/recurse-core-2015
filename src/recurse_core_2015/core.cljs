(ns recurse-core-2015.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(def gmap (.. js/google -maps))
(def Map (.. gmap -Map))
(def Marker (.. gmap -Marker))

(def nyc {:lat 40.73127
          :lng -73.9459})

(defn rand-delta [val delta]
  (+ (- (/ delta 2) (rand delta)) val))
(defn hex [i]
  (if (> i 9)
    (case i
      10 "A"
      11 "B"
      12 "C"
      13 "D"
      14 "E"
      15 "F")
    i))
(defn rand-hex [i]
  (str (hex (quot (rand-int i) 16))
       (hex (mod (rand-int i) 16))))
(defn rand-color []
  (str "#" (rand-hex 255) (rand-hex 255) (rand-hex 255)))

(def mock-types
  ["Chinese"
   "Korean"
   "BBQ"
   "Pizza"
   "Burger"
   "Healthy"
   "Vegan"
   "Dominican"
   "American"
   "Brazilian"])

(def mock-legend
  (into
   {}
   (map
    (fn [id] [id (rand-color)])
    mock-types)))

(defn mock-restaurant []
  {:title "Restaurant"
   :icon  {:strokeColor (get mock-legend (rand-nth mock-types))
           :fillColor (get mock-legend (rand-nth mock-types))
           :path (.. gmap -SymbolPath -CIRCLE)
           :scale 3}
   :position {:lat (rand-delta (:lat nyc)  0.2)
              :lng (rand-delta (:lng nyc)  0.2)}})

(defonce app-state (atom (map mock-restaurant (range 1 100))))

(def map-opts
  {:center nyc
   :zoom   13
   :disableDefaultUI true})

(defn legend-view [[name color]]
  [:div {:class "entry"}
   [:div {:class "circle"
           :style {:background-color color}}]
   [:div {:class "name"} name]])

(defn app []
  [:div {:id "inner"}
   [:div {:id "header"}
    [:div {:id "header-text"}
     "NYC Food Diversity Map"]]
   [:div {:id "legend"}
    [:div {:id "legend-header"}
     "LEGEND"]
    (map legend-view mock-legend)]])

(def view
  (new Map
       (.getElementById js/document "map")
       (clj->js map-opts)))

(defn make-marker [marker]
  (new Marker (clj->js (assoc marker :map view))))

(def markers (map make-marker @app-state))

;; For some reason markers don't work unless I print them?
(println markers)
(r/render [app]
          (js/document.getElementById "app"))

(defn on-js-reload [])
