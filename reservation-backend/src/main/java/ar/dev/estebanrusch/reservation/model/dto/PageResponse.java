package ar.dev.estebanrusch.reservation.model.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Wrapper genérico de paginación que se devuelve al cliente.
 * Envuelve el Page<T> de Spring en un DTO propio para no exponer
 * detalles internos del framework en la API pública.
 *
 * @param content        lista de elementos de la página actual
 * @param page           número de página actual (base 0)
 * @param size           cantidad de elementos por página
 * @param totalElements  total de registros que coinciden con el filtro
 * @param totalPages     total de páginas disponibles
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    /**
     * Factory method: construye el DTO a partir del Page de Spring
     * mapeando cada elemento con la función toDto.
     */
    public static <E, D> PageResponse<D> from(Page<E> page, java.util.function.Function<E, D> toDto) {
        return new PageResponse<>(
                page.getContent().stream().map(toDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
