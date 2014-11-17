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

escapeText = (str) ->
  '\"' + str.replace(/\s/g,"\\ ") + '\"';

package_folder = path.join( __dirname, '..')

exports.config = config = JSON.parse( fs.readFileSync( path.normalize __dirname + '/../config.json' ) )

exports.getConcepts = getConcepts = (docs, options, callback) ->

    args = ['--username ' + escape(config.username), '--password ' + escape(config.password),
            '--email ' + escape(config.email)];


    for key, value of options
      if _.contains(['Q','t','d','D','a','u','K','l','r','i','Y','b','y'], key) and value == true
        args.push("-" + key)
      if _.contains(['R','e','J','k'], key) and typeIsArray(value) == true
        args.push("-" + key + " " + value.join(','))

    command = 'sh ' + path.normalize ( __dirname + '/../SKR_Web_API_V2_1/run.sh') + ' MMCustom ' + args.join(' ')
    filePath = path.normalize ( __dirname + '/../SKR_Web_API_V2_1/examples/temp.txt')

    analyze = (doc) =>
      args.push("--document " + escape(doc).replace(/\s/g,"_"))
      command = 'sh ' + path.normalize ( __dirname + '/../SKR_Web_API_V2_1/run.sh') + ' MMCustom ' + args.join(' ')
      console.log command
      proc = child_process.execAsync(command, {
          cwd: package_folder
        })
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

    return resultSet.nodeify(callback)
