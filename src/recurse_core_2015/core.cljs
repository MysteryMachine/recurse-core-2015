(ns recurse-core-2015.core)

(enable-console-print!)

(def gmap (.. js/google -maps))
(def Map (.. gmap -Map))
(def Marker (.. gmap -Marker))

(def nyc {:lat 40.73127
          :lng -73.9459})

(defn rand-delta [val delta]
  (+ (- (/ delta 2) (rand delta)) val))

(def mock-types
  ["Chinese"
   "Korean"
   "BBQ"
   "Pizza"
   "Burger"])

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

(defn mock-restaurant []
  {:title "Restaurant"
   :icon  {:strokeColor (rand-color)
           :path (.. gmap -SymbolPath -CIRCLE)
           :scale 3}
   :position {:lat (rand-delta (:lat nyc)  0.2)
              :lng (rand-delta (:lng nyc)  0.2)}})

(defonce app-state (atom (map mock-restaurant (range 1 100))))

(def map-opts
  {:center nyc
   :zoom   13})

(def view
  (new Map
       (.getElementById js/document "map")
       (clj->js map-opts)))

(defn make-marker [marker]
  (new Marker (clj->js (assoc marker :map view))))

(def markers (map make-marker @app-state))

(println markers)
(defn on-js-reload [])
