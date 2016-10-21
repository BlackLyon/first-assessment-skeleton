import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let chalk = require('chalk')
let previouscmd

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username> [host] [port]')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username
    const h = (args.host === undefined) ? ('localhost') : (args.host)
    const p = (args.port === undefined) ? (8080) : (args.port)

    server = connect({ host: h, port: p }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })

    server.on('data', (buffer) => {
      const msg = Message.fromJSON(buffer)

      if (msg.command === 'echo') {
        this.log(chalk.green(msg.toString()))
      } else if (msg.command === 'broadcast') {
        this.log(chalk.white(msg.toString()))
      } else if (msg.command === '@') {
        this.log(chalk.blue(msg.toString()))
      } else if (msg.command === 'users') {
        this.log(chalk.yellow(msg.toString()))
      } else if (msg.command === 'servermsg') {
        this.log(chalk.bold.red(msg.toString()))
      }
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    //const [ command, ...rest ] = words(input, /[^, ]+/g)
    const str = input
    const [ command, ...rest ] = str.split(' ')
    const contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      previouscmd = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'users') {
      previouscmd = command
      server.write(new Message({username, command}).toJSON() + '\n')
    } else if (command[0] === '@') {
      previouscmd = command
      server.write(new Message({username, command: command[0], contents: [command.slice(1), contents].join(' ')})
      .toJSON() + '\n')
    } else if (command === 'broadcast') {
      previouscmd = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      const str = input
      const [...rest] = str.split(' ')
      const contents = rest.join(' ')
      const command = previouscmd

      if (command === 'echo') {
        server.write(new Message({ username, command, contents }).toJSON() + '\n')
      } else if (command === 'users') {
        server.write(new Message({username, command}).toJSON() + '\n')
      } else if (command[0] === '@') {
        server.write(new Message({username, command: command[0], contents: [command.slice(1), contents].join(' ')})
        .toJSON() + '\n')
      } else if (command === 'broadcast') {
        server.write(new Message({ username, command, contents }).toJSON() + '\n')
      } else {
        this.log(`Command <${command}> was not recognized`)
      }
    }

    callback()
  })
