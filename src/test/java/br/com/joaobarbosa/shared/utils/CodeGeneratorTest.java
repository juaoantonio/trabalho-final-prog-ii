package br.com.joaobarbosa.shared.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorTest {
    @Test
    @DisplayName("Deve gerar um código com o tamanho especificado")
    void testGenerateCodeWithSpecifiedLength() {
        int length = 10;
        String code = CodeGenerator.generateCode(length);
        assertNotNull(code);
        assertEquals(length, code.length());
    }
}
