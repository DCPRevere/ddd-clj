(ns ddd-clj.core
  (:gen-class))

(defprotocol Aggregate
  (aggregate-behaviour [args])

(defrecord Command [agg-id data])
(defrecord OtherCommand [agg-id data])

(defmethod Handle Command [command])
(defmethod Handle OtherCommand [command])

(defrecord Event [agg-id data])
(defrecord OtherEvent [agg-id data])

(defmethod apply-event Event [state event])
(defmethod apply-event OtherEvent [state event])

(defprotocol CommandHandler
  (perform [command state]))

(defmulti apply-event
  (fn [state event] (class event)))
