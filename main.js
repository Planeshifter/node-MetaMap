var BPromise, corpus, data, delim, extractConcepts, fs, i, key, metaMap, mime, mime_type, options, program, value;

program = require('commander');

mime = require('mime');

fs = require('fs');

metaMap = require('./lib/metaMap');

BPromise = require('bluebird');


/*
Command-Line-Interface:
 */

program.version('0.1.1').option('-i, --input [value]', 'Load data from disk').option('-l, --list <items>', 'A list of input texts').option('-o, --output [value]', 'Write results to file').option('-d, --delim [value]', 'Delimiter to split text into documents').option('-p, --pretty', 'Pretty print of JSON output').parse(process.argv);

options = {};

if (program.args.length > 0) {
  if (program.args.length % 2 === 1) {
    throw new Error("Wrong number of supplied arguments (always pass key-value pairs)");
  } else {
    i = 0;
    while (i < program.args.length) {
      key = program.args[i];
      value = program.args[i + 1];
      switch (value) {
        case "false":
          value = false;
          break;
        case "true":
          value = true;
      }
      if (Array.isArray(JSON.parse(value))) {
        value = JSON.parse(value);
      }
      options[key] = value;
      i += 2;
    }
  }
}

extractConcepts = function(corpus) {
  var res;
  return res = BPromise.all(corpus.map((function(_this) {
    return function(doc) {
      return metaMap.getConcepts(doc, options);
    };
  })(this))).then((function(_this) {
    return function(data) {
      var outputJSON;
      console.log(data);
      outputJSON = program.pretty ? JSON.stringify(data, null, 2) : JSON.stringify(data);
      if (program.output) {
        return fs.writeFileSync(program.output, outputJSON);
      } else {
        return console.log(outputJSON);
      }
    };
  })(this));
};

corpus;

delim = program.delim;

if (program.list) {
  delim = delim || ";";
  corpus = program.list.split(delim);
  extractConcepts(corpus);
} else if (program.input) {
  data = fs.readFileSync(program.input);
  mime_type = mime.lookup(program.input);
  switch (mime_type) {
    case "text/plain":
      delim = delim || " ";
      corpus = String(data).replace(/\r\n?/g, "\n").split(delim).clean("");
      extractConcepts(corpus);
      break;
    case "text/csv":
      csv.parse(String(data), (function(_this) {
        return function(err, output) {
          corpus = output.map(function(d) {
            return d[0];
          });
          return extractConcepts(corpus);
        };
      })(this));
  }
}
