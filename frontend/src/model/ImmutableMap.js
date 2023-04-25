
export default class ImmutableMap {

  constructor(map) {
    this.map = map ?? new Map()
  }

  get(key) {
    return this.map.get(key)
  }

  has(key) {
    return this.map.has(key)
  }

  set(key, value) {
    return new ImmutableMap(
      new Map([...this.map].concat([[key, value]]))
    )
  }

  modify(key, mapper) {
    const value = this.map.get(key)
    return new ImmutableMap(
      new Map([...this.map].concat([[key, mapper(value)]]))
    )
  }

  modifyIfPresent(key, mapper) {
    const value = this.map.get(key)
    if (!value) return this
    return new ImmutableMap(
      new Map([...this.map].concat([[key, mapper(value)]]))
    )
  }

}

