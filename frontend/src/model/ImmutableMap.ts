
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

}

