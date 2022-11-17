package io.github.parkjeongwoong.entity.CompositeKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityIndexKey implements Serializable {
    private long documentId;
    private long counterDocumentId;
}
