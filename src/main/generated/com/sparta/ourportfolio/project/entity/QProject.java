package com.sparta.ourportfolio.project.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 1985615471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath people = createString("people");

    public final com.sparta.ourportfolio.portfolio.entity.QPortfolio portfolio;

    public final StringPath position = createString("position");

    public final ListPath<ProjectImage, QProjectImage> projectImageList = this.<ProjectImage, QProjectImage>createList("projectImageList", ProjectImage.class, QProjectImage.class, PathInits.DIRECT2);

    public final StringPath term = createString("term");

    public final StringPath title = createString("title");

    public final com.sparta.ourportfolio.user.entity.QUser user;

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.portfolio = inits.isInitialized("portfolio") ? new com.sparta.ourportfolio.portfolio.entity.QPortfolio(forProperty("portfolio"), inits.get("portfolio")) : null;
        this.user = inits.isInitialized("user") ? new com.sparta.ourportfolio.user.entity.QUser(forProperty("user")) : null;
    }

}

