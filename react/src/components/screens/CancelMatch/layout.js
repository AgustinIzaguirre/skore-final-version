import React from 'react';
import PropTypes from 'prop-types';
import WithLoading from '../../hocs/WithLoading';
import WithError from '../../hocs/WithError';
import MatchPage from '../MatchPage';

const CancelMatch = ({ matchKey, message }) => {
    return (
        <MatchPage matchKey={matchKey} message={message} />    
    );
}

CancelMatch.propTypes = {
    message: PropTypes.string.isRequired,
    matchKey: PropTypes.string.isRequired
}

export default WithError(WithLoading(CancelMatch));