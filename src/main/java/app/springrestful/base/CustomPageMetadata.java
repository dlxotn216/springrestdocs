package app.springrestful.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by taesu at : 2019-02-26
 *
 * 여기에 CustomPageMetadata 클래스에 대한 설명을 기술해주세요
 *
 * @author taesu
 * @version 1.0
 * @since 1.0
 */

public class CustomPageMetadata extends PagedResources.PageMetadata {
    
    @XmlAttribute
    @JsonProperty
    private long size;
    
    @XmlAttribute
    @JsonProperty
    private long totalElements;
    
    @XmlAttribute
    @JsonProperty
    private long totalPages;
    
    @XmlAttribute
    @JsonProperty
    private long number;
    
    @XmlAttribute
    @JsonProperty
    private boolean first;

    @XmlAttribute
    @JsonProperty
    private boolean last;

    @XmlAttribute
    @JsonProperty
    private boolean empty;

    public CustomPageMetadata(Page page) {
        super(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
    
    @Override
    public String toString() {
        return "CustomPageMetadata{" +
                "size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", number=" + number +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CustomPageMetadata that = (CustomPageMetadata) o;

        if (size != that.size) return false;
        if (totalElements != that.totalElements) return false;
        if (totalPages != that.totalPages) return false;
        if (number != that.number) return false;
        if (first != that.first) return false;
        if (last != that.last) return false;
        return empty == that.empty;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (totalElements ^ (totalElements >>> 32));
        result = 31 * result + (int) (totalPages ^ (totalPages >>> 32));
        result = 31 * result + (int) (number ^ (number >>> 32));
        result = 31 * result + (first ? 1 : 0);
        result = 31 * result + (last ? 1 : 0);
        result = 31 * result + (empty ? 1 : 0);
        return result;
    }
}
