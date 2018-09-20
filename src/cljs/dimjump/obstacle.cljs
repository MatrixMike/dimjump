(ns dimjump.obstacle
  (:require [quil.core :as q :include-macros true]
            [dimjump.data :as data :refer [constants]]))

(defn spawn [{:keys [speed x y] :or {speed 0} :as opts}]
  (merge {:move-x (- speed)
          :move-y (- speed)}
         opts))

(defn draw [{:keys [x w h y]} ctx]
  (let [floor-y (:floor-y constants)]
    (.beginPath ctx)
    (.rect ctx x (- y h) w h)
    (.closePath ctx)
    (.fill ctx)))

(defn next-x [{:keys [x min-x max-x move-x]}]
  (max min-x (min max-x (+ x move-x))))

(defn next-y [{:keys [y min-y max-y move-y]}]
  (max min-y (min max-y (+ y move-y))))

(defn next-move-x [{:keys [x min-x max-x move-x]}]
  (if (or
        (and (> move-x 0) (>= x max-x))
        (and (< move-x 0) (<= x min-x)))
    (- move-x)
    move-x))

(defn next-move-y [{:keys [y min-y max-y move-y]}]
  (if (or
        (and (> move-y 0) (>= y max-y))
        (and (< move-y 0) (<= y min-y)))
    (- move-y)
    move-y))

(defn progress [obstacle]
  "Updates position based on min-x and min-y, if necessary"
  (assoc obstacle
         :x (next-x obstacle)
         :y (next-y obstacle)
         :move-x (next-move-x obstacle)
         :move-y (next-move-y obstacle)))

(defn collision? [{px :x py :y pw :w ph :h} {ox :x oy :y ow :w oh :h}]
  "Returns true if any obstacle in the level has collided with the
   given entity (the player). Currently operates on very basic 2D rectangles
   and does not support bounding boxes on rotated shapes."
  (let [py-top (- py (/ ph 2))
        py-bottom (+ py (/ ph 2))
        px-left (- px (/ pw 2))
        px-right (+ px (/ pw 2))]
    (and (< px-left (+ ox ow))
         (< ox px-right)
         (< py-top oy)
         (< (- oy oh) py-bottom))))
