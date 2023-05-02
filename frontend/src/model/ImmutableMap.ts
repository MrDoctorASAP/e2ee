
/**
 * Декоратор для Map.
 * Кадый раз при вставке/обновлении значений создаёт новый обьект, копируя все значения.
 * Необходимость такого класса обусловленна политикой обновления состояний в React:
 * При обновлении состояния необходимо передавать новый обьект.
 * */
export default class ImmutableMap<K, V> {

  map: Map<K, V>

  constructor(map?: Map<K, V>) {
    this.map = map ?? new Map<K, V>()
  }

  get(key: K) {
    return this.map.get(key)
  }

  has(key: K) {
    return this.map.has(key)
  }

  set(key: K, value: V) {
    return new ImmutableMap(
      new Map(Array.from(this.map.entries()).concat([[key, value]]))
    )
  }

  modify(key: K, mapper: (v: V|undefined) => V) {
    const value = this.map.get(key);
    return new ImmutableMap(
      new Map(Array.from(this.map.entries()).concat([[key, mapper(value)]]))
    )
  }

  modifyIfPresent(key: K, mapper: (v: V) => V) {
    const value = this.map.get(key)
    if (!value) return this
    return new ImmutableMap(
      new Map(Array.from(this.map.entries()).concat([[key, mapper(value)]]))
    )
  }

  setAll(entries: [K, V][]) {
    return new ImmutableMap(
      new Map(Array.from(this.map.entries()).concat(entries))
    )
  }

  values() : IterableIterator<V> {
    return this.map.values()
  }

  entries() : IterableIterator<[K, V]> {
    return this.map.entries()
  }

}

