prompt = require('prompt')
fs = require('fs')

schema = {
  properties: {
    username: {
      pattern: /^[a-zA-Z\s\-]+$/,
      message: 'Name must be only letters, spaces, or dashes',
      required: true
    },
    password: {
      hidden: true
    }
  }
}


prompt.start()

prompt.get(['username', 'email', 'password'], (err, result) ->
  console.log('Command-line input received:')
  console.log('  username: ' + result.username)
  console.log('  email: ' + result.email)
  fs.writeFileSync('./config.json', JSON.stringify(result) )
)
