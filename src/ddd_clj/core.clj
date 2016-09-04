(ns ddd-clj.core
  (:gen-class))





(defprotocol DomainRepo
  (get-by-id [dr id])
  (save [dr agg]))

(defrecord EventStore []
  DomainRepo
  (get-by-id [dr id] (...))
  (save [dr agg] (...)))





(defprotocol Aggregate)

(defrecord EgAgg [id]
  Aggregate)

(defmulti apply-event
  (fn [agg event]
    [(type agg) (type event)]))

(defmethod apply-event [ddd-clj.core.EgAgg ddd-clj.core.EgEvent]
  [agg event])





(defprotocol Command
  (handle [command]))

(defrecord EgCommand [agg-id]
  Command
  (handle [command]))





(defprotocol Event)

(defrecord EgEvent [agg-id data]
  Event)
