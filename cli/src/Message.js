export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents, receiver }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.receiver = receiver
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      receiver: this.receiver
    })
  }

  toString () {
    return this.contents
  }
}
