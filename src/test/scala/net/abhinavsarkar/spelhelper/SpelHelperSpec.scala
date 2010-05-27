package net.abhinavsarkar.spelhelper

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.ShouldMatchersForJUnit

@RunWith(classOf[JUnitRunner])
class SpelHelperSpec extends Spec with ShouldMatchersForJUnit {
  describe("SpelHelper") {

    it ("should register and evaluate functions ") {
      new SpelHelper()
        .registerFunctionsFromClass(classOf[Functions])
        .evalExpression(
          "#test('check')", new {}, classOf[String]) should equal("check")
    }

    it ("should register implicit methods ") {
      new SpelHelper()
        .registerImplicitMethodsFromClass(classOf[Functions])
        .lookupImplicitMethod("java.lang.String.test") should equal(
          classOf[Functions].getMethod("test", classOf[String]))
    }

    it ("should register implicit constructors ") {
      new SpelHelper()
        .registerConstructorsFromClass(classOf[Functions])
        .lookupImplicitConstructor("Functions[]") should equal(
          classOf[Functions].getConstructor())
    }

    it ("should evaluate implicit methods ") {
      new SpelHelper()
        .registerImplicitMethodsFromClass(classOf[Functions])
        .evalExpression(
          "'check'.test()", new {}, classOf[String]) should equal("check")
    }

    it ("should evaluate implicit constructors ") {
      new SpelHelper()
        .registerConstructorsFromClass(classOf[Functions])
        .evalExpression(
          "new Functions()", new {}, classOf[Functions]) should equal(new Functions)
    }

    it ("should evaluate implicit properties ") {
      new SpelHelper().evalExpression(
        "'abc'.hashCode", new {}, classOf[int]) should equal("abc".hashCode) 
    }

  }
}

