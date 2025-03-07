package com.acautomaton.forum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "article")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EsArticle {
    @Id
    @Field(type = FieldType.Integer)
    private Integer id;
    @Field(type = FieldType.Integer)
    private Integer owner;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String ownerNickname;
    @Field(type = FieldType.Keyword)
    private String ownerAvatar;
    @Field(type = FieldType.Integer)
    private Integer topic;
    @Field(type = FieldType.Keyword)
    private String topicTitle;
    @Field(type = FieldType.Keyword)
    private String topicAvatar;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Keyword)
    private String firstImage;
    @Field(type = FieldType.Integer)
    private Integer visits;
    @Field(type = FieldType.Integer)
    private Integer thumbsUp;
    @Field(type = FieldType.Integer)
    private Integer collections;
    @Field(type = FieldType.Integer)
    private Integer tipping;
    @Field(type = FieldType.Integer)
    private Integer forwards;
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
