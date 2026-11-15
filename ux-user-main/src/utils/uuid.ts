/** Generate a request identifier in browsers without crypto.randomUUID (for example HTTP deployments). */
export function generateUUID() {
  let uuid = ''
  for (let index = 0; index < 32; index += 1) {
    const random = (Math.random() * 16) | 0
    if (index === 8 || index === 12 || index === 16 || index === 20) uuid += '-'
    uuid += (index === 12 ? 4 : index === 16 ? (random & 3) | 8 : random).toString(16)
  }
  return uuid
}
