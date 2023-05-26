package com.sparta.ourportfolio.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1033442945L;

    public static final QUser user = new QUser("user");

    public final com.sparta.ourportfolio.common.utils.QTimeStamped _super = new com.sparta.ourportfolio.common.utils.QTimeStamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<com.sparta.ourportfolio.portfolio.entity.Portfolio, com.sparta.ourportfolio.portfolio.entity.QPortfolio> portfolioList = this.<com.sparta.ourportfolio.portfolio.entity.Portfolio, com.sparta.ourportfolio.portfolio.entity.QPortfolio>createList("portfolioList", com.sparta.ourportfolio.portfolio.entity.Portfolio.class, com.sparta.ourportfolio.portfolio.entity.QPortfolio.class, PathInits.DIRECT2);

    public final StringPath profileImage = createString("profileImage");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

