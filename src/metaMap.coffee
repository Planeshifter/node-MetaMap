BPromise = require 'bluebird'
_ = require 'underscore'
util = require 'util'
fs = BPromise.promisifyAll( require 'fs' )
path = require 'path'
child_process = BPromise.promisifyAll( require 'child_process' )
parseStringAsync = BPromise.promisify( require('xml2js').parseString )

typeIsArray = Array.isArray || ( value ) -> return {}.toString.call( value ) is '[object Array]'

Array::removeAll = (v) -> x for x in @ when x!=v

escape = (str) ->
  '"' + str + '"'

package_folder = path.join( __dirname, '..')

exports.config = config = JSON.parse(fs.readFileSync __dirname + '/../config.json')

exports.getConcepts = getConcepts = (docs, options, callback) ->

    args = ['--username ' + escape(config.username), '--password ' + escape(config.password),
            '--email ' + escape(config.email)];
    command = 'sh SKR_Web_API_V2_1/run.sh MMCustom ' + args.join(' ')

    analyze = (doc) =>
      pWrite = fs.writeFileAsync('temp.txt', doc)
      proc = pWrite.then( () =>
        return child_process.execAsync(command, {
          cwd: package_folder
        })
      )
      return proc

    if Array.isArray(docs) == false then docs = Array(docs);

    analyses = (analyze doc for doc in docs)

    resultSet = BPromise.all(analyses)
      .map( (data) =>
        xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + data[0].split('<?xml version="1.0" encoding="UTF-8"?>')[1]
        return parseStringAsync(xmlString, {
            mergeAttrs: true,
            explicitArray: false
          })
      )
      .map( (data) =>
        ret = data.MMOs.MMO
        delete ret.CmdLine
        return ret
      )

    BPromise.all(resultSet).then () => fs.unlink("temp.txt")
    return resultSet.nodeify(callback)
