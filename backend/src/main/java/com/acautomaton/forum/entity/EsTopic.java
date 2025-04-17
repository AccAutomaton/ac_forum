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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "topic")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EsTopic {
    @Id
    @Field(type = FieldType.Integer)
    private Integer id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
    @Field(type = FieldType.Integer)
    private Integer administrator;
    @Field(type = FieldType.Integer)
    private Integer articles;
    @Field(type = FieldType.Integer)
    private Integer visits;
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Field(type = FieldType.Keyword)
    private String avatar;

    public EsTopic(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.description = topic.getDescription();
        this.administrator = topic.getAdministrator();
        this.articles = topic.getArticles();
        this.visits = topic.getVisits();
        this.createTime = topic.getCreateTime();
        this.avatar = topic.getAvatar();
    }

    public EsTopic(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public static List<EsTopic> toEsTopicList(List<Topic> topics) {
        List<EsTopic> esTopics = new ArrayList<>();
        for (Topic topic : topics) {
            esTopics.add(new EsTopic(topic));
        }
        return esTopics;
    }

    public static List<EsTopic> getIdAndTitle(List<EsTopic> topics) {
        List<EsTopic> esTopics = new ArrayList<>();
        for (EsTopic topic : topics) {
            esTopics.add(new EsTopic(topic.getId(), topic.getTitle()));
        }
        return esTopics;
    }
}
