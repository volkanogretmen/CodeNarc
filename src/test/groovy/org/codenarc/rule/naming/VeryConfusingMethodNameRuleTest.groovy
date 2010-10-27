/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for VeryConfusingMethodNameRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class VeryConfusingMethodNameRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "VeryConfusingMethodName"
    }

    void testNoViolations() {
        final SOURCE = '''
        	def foo() {}
        	def foo(int x) {}
            def bar() {}
            def bar(int x) {}
            def baz = {}
            def bif = {}
            class MyClass {
                def foo() {}
                def foo(int x) {}
                def bar() {}
                def bar(int x) {}
                def baz = {}
                def bif = {}
                def x = new Object() {
                    def foo() {}
                    def foo(int x) {}
                    def y = new Object() {
                        def foo() {}
                        def foo(int x) {}
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }


    void test2MethodViolationsInScript() {
        final SOURCE = '''
        	def foo() {}
        	def foo(int x) {}
            def Foo() {}
            def foO() {}
        '''
        assertTwoViolations(SOURCE,
                4, 'def Foo() {}',
                5, 'def foO() {}')
    }

    void test2ClosureViolationsInScript() {
        final SOURCE = '''
        	def foo = {}
            def Foo = {}
            def foO = {}
        '''
        // it is too hard to trap this condition. Must let it succeed
        assertNoViolations(SOURCE)
    }

    // TODO: this condition can only be found in the CLASS_GENERATION compile phase
    // for now the test is ignored
    void ignore_test2ClosureViolationsInClass() {
        final SOURCE = '''
            class MyClass {
            	def foo = {}
                Closure FOo
                def foO = {}
            }
        '''
        assertTwoViolations(SOURCE, 
                4, 'Closure FOo',
                5, 'def foO = {}')
    }

    void test2ViolationsInClass() {
        final SOURCE = '''
            class MyClass {
                def Foo = {}        // this one is a closure!
                def foo() {}
                def foO() {}
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'def foo() {}',
                5, 'def foO() {}')
    }

    void test2ViolationsInClassWithOverloading() {
        final SOURCE = '''
            class MyClass {
                def foo() {}
                def foo(int x) {}
                def foO() {}
                def foO(int x) {}
            }
        '''
        assertTwoViolations(SOURCE,
                5, 'def foO() {}',
                6, 'def foO(int x) {}')
    }

    void test2ViolationsInNestedClasses() {
        final SOURCE = '''
            class MyClass {
                def foo() {}
                def foO() {}

                def x = new Object() {
                    def innerFoo() {}
                    def innerfoO = {} // this one is a closure!
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'def foO() {}',
                7, 'def innerFoo() {}') // this seems out of order but is correct
    }

    void testDeepNesting() {
        final SOURCE = '''
        	def foo() {}
        	def foo(int x) {}
            class MyClass {
                def foo() {}
                def foo(int x) {}
                def x = new Object() {
                    def foo() {}
                    def foo(int x) {}
                    def y = new Object() {
                        def foo = {}
                        def FoO = {}
                        def foO() {}
                    }
                }
            }
        '''
        assertTwoViolations(SOURCE,
                12, 'def FoO = {}',
                13, 'def foO() {}')
    }

    protected Rule createRule() {
        return new VeryConfusingMethodNameRule()
    }

}