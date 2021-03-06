import React from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import i18next from 'i18next';
import PropTypes from 'prop-types';
import Loader from '../../Loader';
import Account from './components/Account';
import AccountPropType from '../../../proptypes/AccountPropType';
import SearchBar from './components/SearchBar';
import WithError from '../../hocs/WithError';
import WithExecuting from '../../hocs/WithExecuting';
import WithLoading from '../../hocs/WithLoading';

const Accounts = ({ accounts, getUsers, hasMore, onSubmit, filters,
                    likes, likeUser, dislikeUser, currentUser, history }) => {
    const inputStyle = "form-control filter-input mb-2";
    return (
        <div>
            <div className="row text-center mt-5">
                <div className="col">
                    <h1>
                        {i18next.t('accounts.title')}
                    </h1>
                </div>
            </div>
            <div className="container">
                <SearchBar onSubmit={onSubmit} inputStyle={inputStyle}
                            label={i18next.t('createUserForm.username')}
                            initialValues={filters} />
            </div>
            <div className="container">
                <InfiniteScroll dataLength={accounts.length} style={{height: 'auto', overflow: 'visible'}} next={getUsers}
                                hasMore={hasMore} loader={<Loader />} >
                    {accounts.map((account, i) => 
                        <Account key={i} account={account} isLogged={currentUser}
                                    isLiked={likes[account.username]} likeUser={likeUser}
                                    dislikeUser={dislikeUser} history={history} />)}
                </InfiniteScroll> 
            </div>
        </div>
    );
}

Accounts.propTypes = {
    accounts: PropTypes.arrayOf(AccountPropType).isRequired,
    getUsers: PropTypes.func.isRequired,
    hasMore: PropTypes.bool.isRequired,
    filters: PropTypes.object.isRequired,
    likes: PropTypes.object.isRequired,
    likeUser: PropTypes.func.isRequired,
    dislikeUser: PropTypes.func.isRequired,
    currentUser: PropTypes.string,
    history: PropTypes.object.isRequired
}

export default WithError(WithExecuting(WithLoading(Accounts)));