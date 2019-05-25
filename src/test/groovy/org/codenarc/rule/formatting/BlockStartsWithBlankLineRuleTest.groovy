/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for BlockStartsWithBlankLineRule
 *
 * @author Marcin Erdmann
 */
class BlockStartsWithBlankLineRuleTest extends AbstractRuleTestCase<BlockStartsWithBlankLineRule> {

    private static final String MESSAGE = 'Code block starts with a blank line.'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'BlockStartsWithBlankLine'
    }

    @Test
    void testMethodsNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void singleLineMethod() { }

                void emptyMethod() {
                }

                String startingWithNonEmptyLine() {
                    "value"
                }
            }
        '''
    }

    @Test
    void testIfElseNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void method() {
                    if (true) false
                    if (true) {
                        'true'
                    } else { 'false'

                    }
                    if (false) {
                    }
                }
            }
        '''
    }

    @Test
    void testSingleMethodViolation() {
        final SOURCE = '''
            class InvalidClass {
                int startingWithBlankLine() {

                    return 1
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, '', MESSAGE)
    }

    @Test
    void testSingleIfViolation() {
        final SOURCE = '''
            class InvalidClass {
                int method() {
                    if (true) {

                        1
                    }
                }
            }
        '''

        assertSingleViolation(SOURCE, 5, '', MESSAGE)
    }

    @Test
    void testSingleIfElseViolations() {
        final SOURCE = '''
            class InvalidClass {
                int method() {
                    if (true) {

                        1
                    } else {

                        2
                    }
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 5, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 8, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testSingleClosureViolation() {
        final SOURCE = '''
            class InvalidClass {
                def methodWithClosure() {
                    { ->

                        1
                    }
                }
            }
        '''

        assertSingleViolation(SOURCE, 5, '',
            MESSAGE)
    }

    @Test
    void testMultipleClosureViolations() {
        final SOURCE = '''
            class InvalidClass {
                def methodWithClosures() {
                    def list = []
                    list.each { item ->

                        println
                    }

                    list.each {

                        1
                    }
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 6, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 11, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testNestedViolations() {
        final SOURCE = '''
            class InvalidClass {
                int method() {

                    if (true) {

                        for (i = 0; i < 1; i++) {

                            for (item in [1]) {

                                while (false) {

                                    { ->

                                        try {

                                            1
                                        } catch (e) {

                                            2
                                        } finally {

                                            3
                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        switch (false) {

                            default:
                                4
                        }
                    }
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 4, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 6, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 8, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 10, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 12, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 14, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 16, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 19, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 22, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 30, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 32, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testMultipleMethodViolations() {
        final SOURCE = '''
            class InvalidClass {
                InvalidClass() {

                }

                void second() {

                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 4, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 8, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testMapCoercionBlocks() {
        final SOURCE = '''
            def myService = [
            getCount:{ id, name ->

                assert id == 99
                assert name == 'Joe'
                return 1
            },
            doStuff: {

            }
        ] as MyService
        '''

        assertViolations(SOURCE,
                [lineNumber: 4, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 10, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testKnownLimitations() {
        assertNoViolations '''
            class KnownLimitationsClass {
                boolean oddlyFormattedMethod() { true

                }

                String startingWithNonEmptyLine() {
                    "value"
                }

                def methodWithClosures() {
                    def list = []
                    list.each { item
                        ->

                        1
                    }

                    list.each {
                        item ->

                    }

                    [:].each {
                        def key,
                        def value ->

                    }

                }
            }
        '''
    }

    @Override
    protected BlockStartsWithBlankLineRule createRule() {
        new BlockStartsWithBlankLineRule()
    }
}
