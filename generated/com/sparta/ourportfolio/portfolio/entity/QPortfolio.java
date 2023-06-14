package com.sparta.ourportfolio.portfolio.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPortfolio is a Querydsl query type for Portfolio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPortfolio extends EntityPathBase<Portfolio> {

    private static final long serialVersionUID = -1652651569L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPortfolio portfolio = new QPortfolio("portfolio");

    public final StringPath blogUrl = createString("blogUrl");

    public final StringPath category = createString("category");

    public final StringPath email = createString("email");

    public final StringPath filter = createString("filter");

    public final StringPath githubId = createString("githubId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath intro = createString("intro");

    public final StringPath location = createString("location");

    public final StringPath portfolioImage = createString("portfolioImage");

    public final StringPath portfolioTitle = createString("portfolioTitle");

    public final ListPath<com.sparta.ourportfolio.project.entity.Project, com.sparta.ourportfolio.project.entity.QProject> projectList = this.<com.sparta.ourportfolio.project.entity.Project, com.sparta.ourportfolio.project.entity.QProject>createList("projectList", com.sparta.ourportfolio.project.entity.Project.class, com.sparta.ourportfolio.project.entity.QProject.class, PathInits.DIRECT2);

    public final StringPath residence = createString("residence");

    public final StringPath techStack = createString("techStack");

    public final StringPath telephone = createString("telephone");

    public final com.sparta.ourportfolio.user.entity.QUser user;

    public final StringPath youtubeUrl = createString("youtubeUrl");

    public QPortfolio(String variable) {
        this(Portfolio.class, forVariable(variable), INITS);
    }

    public QPortfolio(Path<? extends Portfolio> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPortfolio(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPortfolio(PathMetadata metadata, PathInits inits) {
        this(Portfolio.class, metadata, inits);
    }

    public QPortfolio(Class<? extends Portfolio> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.sparta.ourportfolio.user.entity.QUser(forProperty("user")) : null;
    }

}

