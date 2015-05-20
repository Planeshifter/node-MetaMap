(function() {
  var R, getConceptArray;

  R = require('ramda');

  getConceptArray = function(resultSet) {
    var _ref, _ref1, _ref2;
    return R.flatten((_ref = resultSet[0]) != null ? (_ref1 = _ref.Utterances) != null ? (_ref2 = _ref1.Utterance.Phrases) != null ? _ref2.Phrase.map(function(p) {
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
    }) : void 0 : void 0 : void 0);
  };

  module.exports = getConceptArray;

}).call(this);
