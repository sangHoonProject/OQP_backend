package com.example.oqp.content.pagination;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class PaginationResponse<T> {

    private T body;

    private Pagination pagination;
}
