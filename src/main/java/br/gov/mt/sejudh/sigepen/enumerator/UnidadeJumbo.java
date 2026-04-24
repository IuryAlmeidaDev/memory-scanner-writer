package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UnidadeJumbo {

    UNIDADE_2(2L),
    UNIDADE_3(3L),
    UNIDADE_7(7L),
    UNIDADE_8(8L),
    UNIDADE_13(13L),
    UNIDADE_19(19L),
    UNIDADE_51(51L),
    UNIDADE_54(54L),
    UNIDADE_58(58L);

    private final Long id;

    UnidadeJumbo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static List<Long> todosIds() {
        return Arrays.stream(values())
                .map(UnidadeJumbo::getId)
                .collect(Collectors.toList());
    }

    public static boolean contemId(Long id) {
        if (id == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(u -> u.id.equals(id));
    }
}
