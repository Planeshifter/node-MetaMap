(function() {
  var BPromise, child_process, config, escape, fs, getConcepts, package_folder, parseStringAsync, path, typeIsArray, util, _;

  BPromise = require('bluebird');

  _ = require('underscore');

  util = require('util');

  fs = BPromise.promisifyAll(require('fs'));

  path = require('path');

  child_process = BPromise.promisifyAll(require('child_process'));

  parseStringAsync = BPromise.promisify(require('xml2js').parseString);

  typeIsArray = Array.isArray || function(value) {
    return {}.toString.call(value) === '[object Array]';
  };

  Array.prototype.removeAll = function(v) {
    var x, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = this.length; _i < _len; _i++) {
      x = this[_i];
      if (x !== v) {
        _results.push(x);
      }
    }
    return _results;
  };

  escape = function(str) {
    return '"' + str + '"';
  };

  package_folder = path.join(__dirname, '..');

  exports.config = config = JSON.parse(fs.readFileSync(__dirname + '/../config.json'));

  exports.getConcepts = getConcepts = function(docs, options, callback) {
    var analyses, analyze, args, command, doc, resultSet;
    args = ['--username ' + escape(config.username), '--password ' + escape(config.password), '--email ' + escape(config.email)];
    command = 'sh SKR_Web_API_V2_1/run.sh MMCustom ' + args.join(' ');
    analyze = (function(_this) {
      return function(doc) {
        var pWrite, proc;
        pWrite = fs.writeFileAsync('temp.txt', doc);
        proc = pWrite.then(function() {
          return child_process.execAsync(command, {
            cwd: package_folder
          });
        });
        return proc;
      };
    })(this);
    if (Array.isArray(docs) === false) {
      docs = Array(docs);
    }
    analyses = (function() {
      var _i, _len, _results;
      _results = [];
      for (_i = 0, _len = docs.length; _i < _len; _i++) {
        doc = docs[_i];
        _results.push(analyze(doc));
      }
      return _results;
    })();
    resultSet = BPromise.all(analyses).map((function(_this) {
      return function(data) {
        var xmlString;
        xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + data[0].split('<?xml version="1.0" encoding="UTF-8"?>')[1];
        return parseStringAsync(xmlString, {
          mergeAttrs: true,
          explicitArray: false
        });
      };
    })(this)).map((function(_this) {
      return function(data) {
        var ret;
        ret = data.MMOs.MMO;
        delete ret.CmdLine;
        return ret;
      };
    })(this));
    BPromise.all(resultSet).then((function(_this) {
      return function() {
        return fs.unlink("temp.txt");
      };
    })(this));
    return resultSet.nodeify(callback);
  };

}).call(this);
