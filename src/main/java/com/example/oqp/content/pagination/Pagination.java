package com.example.oqp.content.pagination;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    private Integer size;

    private Integer page;

    private Integer element;

    private Long totalElement;

    private Integer totalPage;
}
