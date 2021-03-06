(function() {
  var BPromise, child_process, config, escape, escapeText, fs, getConcepts, package_folder, parseStringAsync, path, typeIsArray, util, _;

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

  escapeText = function(str) {
    return '\"' + str.replace(/\s/g, "\\ ") + '\"';
  };

  package_folder = path.join(__dirname, '..');

  exports.config = config = JSON.parse(fs.readFileSync(path.normalize(__dirname + '/../config.json')));

  exports.getConcepts = getConcepts = function(docs, options, callback) {
    var analyses, analyze, args, command, doc, filePath, key, resultSet, value;
    args = ['--username ' + escape(config.username), '--password ' + escape(config.password), '--email ' + escape(config.email)];
    for (key in options) {
      value = options[key];
      if (_.contains(['Q', 't', 'd', 'D', 'a', 'u', 'K', 'l', 'r', 'i', 'Y', 'b', 'y'], key) && value === true) {
        args.push("-" + key);
      }
      if (_.contains(['R', 'e', 'J', 'k'], key) && typeIsArray(value) === true) {
        args.push("-" + key + " " + value.join(','));
      }
    }
    command = 'sh ' + path.normalize((__dirname + '/../SKR_Web_API_V2_1/run.sh') + ' MMCustom ' + args.join(' '));
    filePath = path.normalize(__dirname + '/../SKR_Web_API_V2_1/examples/temp.txt');
    analyze = function(doc) {
      var proc;
      args.push("--document " + escape(doc).replace(/\s/g, "_"));
      command = 'sh ' + path.normalize((__dirname + '/../SKR_Web_API_V2_1/run.sh') + ' MMCustom ' + args.join(' '));
      proc = child_process.execAsync(command, {
        cwd: package_folder,
        maxBuffer: 1024 * 1024
      });
      return proc;
    };
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
    resultSet = BPromise.all(analyses).map(function(data) {
      var xmlString;
      xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + data[0].split('<?xml version="1.0" encoding="UTF-8"?>')[1];
      return parseStringAsync(xmlString, {
        mergeAttrs: true,
        explicitArray: false
      });
    }).map(function(data) {
      var ret;
      ret = data.MMOs.MMO;
      delete ret.CmdLine;
      return ret;
    });
    return resultSet.nodeify(callback);
  };

  exports.getConceptArray = function(resultSet) {
    var phrases, ret, utterances, _ref, _ref1, _ref2;
    utterances = (_ref = resultSet[0]) != null ? (_ref1 = _ref.Utterances) != null ? _ref1.Utterance : void 0 : void 0;
    if (Array.isArray(utterances) === true) {
      ret = _.flatten(utterances.map(function(u) {
        var phrases, _ref2;
        phrases = (_ref2 = u.Phrases) != null ? _ref2.Phrase : void 0;
        if (!Array.isArray(phrases)) {
          phrases = [phrases];
        }
        return phrases.map(function(p) {
          var _ref3;
          return (_ref3 = p.Mappings) != null ? _ref3.Mapping : void 0;
        }).filter(function(p) {
          return p !== void 0;
        }).map(function(c) {
          if (Array.isArray(c)) {
            return c[0];
          } else {
            return c;
          }
        }).map(function(c) {
          var _ref3;
          return (_ref3 = c.MappingCandidates) != null ? _ref3.Candidate : void 0;
        });
      }));
    } else {
      phrases = (_ref2 = utterances.Phrases) != null ? _ref2.Phrase : void 0;
      if (!Array.isArray(phrases)) {
        phrases = [phrases];
      }
      ret = _.flatten(phrases.map(function(p) {
        var _ref3;
        return (_ref3 = p.Mappings) != null ? _ref3.Mapping : void 0;
      }).filter(function(p) {
        return p !== void 0;
      }).map(function(c) {
        if (Array.isArray(c)) {
          return c[0];
        } else {
          return c;
        }
      }).map(function(c) {
        var _ref3;
        return (_ref3 = c.MappingCandidates) != null ? _ref3.Candidate : void 0;
      }));
    }
    return ret;
  };

}).call(this);
