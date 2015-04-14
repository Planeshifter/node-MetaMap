chai = require "chai"
chaiAsPromised = require "chai-as-promised"
chai.use(chaiAsPromised)
BPromise = require "bluebird"
util = require "util"

expect = chai.expect
metaMap = require '../lib/metaMap.js'

metaMap.config = {
  "username": process.env.USERNAME,
  "password": process.env.PASSWORD,
  "email": process.env.EMAIL
}

console.log process.env

describe "package namespace", () ->
  it "loads successfully" , () =>
     expect(metaMap.getConcepts).to.be.a("function")
     expect(metaMap.config).to.be.a("object")
  it "has config object with username, password and email to connect to API", () =>
    expect(metaMap.config).to.have.property("username")
    expect(metaMap.config).to.have.property("password")
    expect(metaMap.config).to.have.property("email")

describe "getConcepts([corpus]): analzye text array", () ->
  @timeout(10000)
  it "works in vanilla mode", () =>
    fAnalysis = metaMap.getConcepts("Definition and classification of chronic kidney disease")
    fElem = fAnalysis.then((arr) => arr[0])
    tests = []
    tests.push expect(fAnalysis).to.eventually.be.not.empty
    tests.push expect(fElem).to.eventually.have.property "Utterances"
    tests.push expect(fElem).to.eventually.have.deep.property "Utterances.Utterance.Phrases"
    return BPromise.all(tests)
  it "accepts options", () =>
    fRes = metaMap.getConcepts("Definition and classification of chronic kidney disease",{r: ["AIR","AOD"]})
    fElem = fRes.then((arr) => arr[0])
    tests = []
    tests.push expect(fRes).to.eventually.be.not.empty
    tests.push expect(fElem).to.eventually.have.property "Utterances"
    tests.push expect(fElem).to.eventually.have.deep.property "Utterances.Utterance.Phrases"
    return BPromise.all(tests)
