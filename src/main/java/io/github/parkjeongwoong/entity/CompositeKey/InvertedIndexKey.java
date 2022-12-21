package io.github.parkjeongwoong.entity.CompositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvertedIndexKey implements Serializable {
    private long documentId;
    private String term;
}
