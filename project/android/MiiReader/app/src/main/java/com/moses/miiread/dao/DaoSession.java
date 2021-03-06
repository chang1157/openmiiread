package com.moses.miiread.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.moses.miiread.bean.SearchHistoryBean;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.BookmarkBean;
import com.moses.miiread.bean.ReplaceRuleBean;
import com.moses.miiread.bean.UserInfoBean;
import com.moses.miiread.bean.CookieBean;
import com.moses.miiread.bean.BookInfoBean;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.ChapterListBean;

import com.moses.miiread.dao.SearchHistoryBeanDao;
import com.moses.miiread.dao.SearchBookBeanDao;
import com.moses.miiread.dao.BookSourceBeanDao;
import com.moses.miiread.dao.BookmarkBeanDao;
import com.moses.miiread.dao.ReplaceRuleBeanDao;
import com.moses.miiread.dao.UserInfoBeanDao;
import com.moses.miiread.dao.CookieBeanDao;
import com.moses.miiread.dao.BookInfoBeanDao;
import com.moses.miiread.dao.BookShelfBeanDao;
import com.moses.miiread.dao.ChapterListBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig searchHistoryBeanDaoConfig;
    private final DaoConfig searchBookBeanDaoConfig;
    private final DaoConfig bookSourceBeanDaoConfig;
    private final DaoConfig bookmarkBeanDaoConfig;
    private final DaoConfig replaceRuleBeanDaoConfig;
    private final DaoConfig userInfoBeanDaoConfig;
    private final DaoConfig cookieBeanDaoConfig;
    private final DaoConfig bookInfoBeanDaoConfig;
    private final DaoConfig bookShelfBeanDaoConfig;
    private final DaoConfig chapterListBeanDaoConfig;

    private final SearchHistoryBeanDao searchHistoryBeanDao;
    private final SearchBookBeanDao searchBookBeanDao;
    private final BookSourceBeanDao bookSourceBeanDao;
    private final BookmarkBeanDao bookmarkBeanDao;
    private final ReplaceRuleBeanDao replaceRuleBeanDao;
    private final UserInfoBeanDao userInfoBeanDao;
    private final CookieBeanDao cookieBeanDao;
    private final BookInfoBeanDao bookInfoBeanDao;
    private final BookShelfBeanDao bookShelfBeanDao;
    private final ChapterListBeanDao chapterListBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        searchHistoryBeanDaoConfig = daoConfigMap.get(SearchHistoryBeanDao.class).clone();
        searchHistoryBeanDaoConfig.initIdentityScope(type);

        searchBookBeanDaoConfig = daoConfigMap.get(SearchBookBeanDao.class).clone();
        searchBookBeanDaoConfig.initIdentityScope(type);

        bookSourceBeanDaoConfig = daoConfigMap.get(BookSourceBeanDao.class).clone();
        bookSourceBeanDaoConfig.initIdentityScope(type);

        bookmarkBeanDaoConfig = daoConfigMap.get(BookmarkBeanDao.class).clone();
        bookmarkBeanDaoConfig.initIdentityScope(type);

        replaceRuleBeanDaoConfig = daoConfigMap.get(ReplaceRuleBeanDao.class).clone();
        replaceRuleBeanDaoConfig.initIdentityScope(type);

        userInfoBeanDaoConfig = daoConfigMap.get(UserInfoBeanDao.class).clone();
        userInfoBeanDaoConfig.initIdentityScope(type);

        cookieBeanDaoConfig = daoConfigMap.get(CookieBeanDao.class).clone();
        cookieBeanDaoConfig.initIdentityScope(type);

        bookInfoBeanDaoConfig = daoConfigMap.get(BookInfoBeanDao.class).clone();
        bookInfoBeanDaoConfig.initIdentityScope(type);

        bookShelfBeanDaoConfig = daoConfigMap.get(BookShelfBeanDao.class).clone();
        bookShelfBeanDaoConfig.initIdentityScope(type);

        chapterListBeanDaoConfig = daoConfigMap.get(ChapterListBeanDao.class).clone();
        chapterListBeanDaoConfig.initIdentityScope(type);

        searchHistoryBeanDao = new SearchHistoryBeanDao(searchHistoryBeanDaoConfig, this);
        searchBookBeanDao = new SearchBookBeanDao(searchBookBeanDaoConfig, this);
        bookSourceBeanDao = new BookSourceBeanDao(bookSourceBeanDaoConfig, this);
        bookmarkBeanDao = new BookmarkBeanDao(bookmarkBeanDaoConfig, this);
        replaceRuleBeanDao = new ReplaceRuleBeanDao(replaceRuleBeanDaoConfig, this);
        userInfoBeanDao = new UserInfoBeanDao(userInfoBeanDaoConfig, this);
        cookieBeanDao = new CookieBeanDao(cookieBeanDaoConfig, this);
        bookInfoBeanDao = new BookInfoBeanDao(bookInfoBeanDaoConfig, this);
        bookShelfBeanDao = new BookShelfBeanDao(bookShelfBeanDaoConfig, this);
        chapterListBeanDao = new ChapterListBeanDao(chapterListBeanDaoConfig, this);

        registerDao(SearchHistoryBean.class, searchHistoryBeanDao);
        registerDao(SearchBookBean.class, searchBookBeanDao);
        registerDao(BookSourceBean.class, bookSourceBeanDao);
        registerDao(BookmarkBean.class, bookmarkBeanDao);
        registerDao(ReplaceRuleBean.class, replaceRuleBeanDao);
        registerDao(UserInfoBean.class, userInfoBeanDao);
        registerDao(CookieBean.class, cookieBeanDao);
        registerDao(BookInfoBean.class, bookInfoBeanDao);
        registerDao(BookShelfBean.class, bookShelfBeanDao);
        registerDao(ChapterListBean.class, chapterListBeanDao);
    }
    
    public void clear() {
        searchHistoryBeanDaoConfig.clearIdentityScope();
        searchBookBeanDaoConfig.clearIdentityScope();
        bookSourceBeanDaoConfig.clearIdentityScope();
        bookmarkBeanDaoConfig.clearIdentityScope();
        replaceRuleBeanDaoConfig.clearIdentityScope();
        userInfoBeanDaoConfig.clearIdentityScope();
        cookieBeanDaoConfig.clearIdentityScope();
        bookInfoBeanDaoConfig.clearIdentityScope();
        bookShelfBeanDaoConfig.clearIdentityScope();
        chapterListBeanDaoConfig.clearIdentityScope();
    }

    public SearchHistoryBeanDao getSearchHistoryBeanDao() {
        return searchHistoryBeanDao;
    }

    public SearchBookBeanDao getSearchBookBeanDao() {
        return searchBookBeanDao;
    }

    public BookSourceBeanDao getBookSourceBeanDao() {
        return bookSourceBeanDao;
    }

    public BookmarkBeanDao getBookmarkBeanDao() {
        return bookmarkBeanDao;
    }

    public ReplaceRuleBeanDao getReplaceRuleBeanDao() {
        return replaceRuleBeanDao;
    }

    public UserInfoBeanDao getUserInfoBeanDao() {
        return userInfoBeanDao;
    }

    public CookieBeanDao getCookieBeanDao() {
        return cookieBeanDao;
    }

    public BookInfoBeanDao getBookInfoBeanDao() {
        return bookInfoBeanDao;
    }

    public BookShelfBeanDao getBookShelfBeanDao() {
        return bookShelfBeanDao;
    }

    public ChapterListBeanDao getChapterListBeanDao() {
        return chapterListBeanDao;
    }

}
