import React, { Component } from 'react';
import PropTypes from 'prop-types';
import i18next from 'i18next';
import MatchPage from './layout';
import MatchService from '../../../services/MatchService';
import AuthService from '../../../services/AuthService';
import Utils from '../../utils/Utils';
import { SC_UNAUTHORIZED, SC_OK, SC_CONFLICT } from '../../../services/constants/StatusCodesConstants';
import { EC_ALREADY_JOINED } from '../../../services/constants/ErrorCodesConstants';

class MatchPageContainer extends Component {
    mounted = false;
    constructor(props) {
        super(props);
        let matchKey;
        if (this.props && this.props.match && this.props.match.params) {
            matchKey = this.props.match.params.matchKey;
        }
        else {
            matchKey = this.props.matchKey;
        }
        this.state = {
            matchKey: matchKey,
            match: null,
            anonymous: false
        };
    }

    joinMatch = (e, match) => {
        e.stopPropagation();
        const currentUser = AuthService.getCurrentUser();
        if (currentUser) {
            const userId = AuthService.getUserId();
            this.joinMatchLogged(match, userId);
        }
        else {
            this.joinMatchAnonymous(match);
        }
    }

    joinMatchLogged = async (match, userId) => {
        if (this.mounted) {
            this.setState({ executing: true });
        }
        const response = await MatchService.joinMatchWithAccount(match.key, userId);
        if (response.status) {
            if (response.status === SC_UNAUTHORIZED) {
                const status = AuthService.internalLogout();
                if (status === SC_OK) {
                    this.props.history.push(`/login`);
                }
                else {
                    this.setState({ error: status });
                }
            }
            else if (this.mounted) {
                if (response.status === SC_CONFLICT) {
                    if (response.data.errorCode === EC_ALREADY_JOINED) {
                        const errorMessage = i18next.t('confirmAssistance.alreadyJoined');
                        this.setState({ errorMessage: errorMessage, executing: false });
                    }
                    else {
                        const errorMessage = i18next.t('confirmAssistance.matchFullOrPlayed');
                        this.setState({ errorMessage: errorMessage, executing: false });
                    }
                }
                else {
                    this.setState({ error: response.status, executing: false });
                }
            }
        }
        else {
            if (this.mounted) {
                this.setState({ match: response, executing: false });
            }
        }
    }

    joinMatchAnonymous = (match) => {
        if (match.competitive && this.mounted) {
            this.props.history.push(`/authenticatedJoin/${match.key}`);
        }
        else if ( this.mounted) {
            this.setState({ anonymous: true });
        }
    }
    
    cancelMatch = (e, match) => {
        e.stopPropagation();
        if (AuthService.getCurrentUser()) {
            const userId = AuthService.getUserId();
            this.cancelMatchLogged(match, userId);
        }
        else {
            this.setStatus({ status: SC_UNAUTHORIZED });
        }
    }

    cancelMatchLogged = async (match, userId) => {
        if (this.mounted) {
            this.setState({ executing: true });
        }
        const response = await MatchService.cancelMatchWithAccount(match.key, userId);
        if (response.status) {
            if (response.status === SC_UNAUTHORIZED) {
                const status = AuthService.internalLogout();
                if (status === SC_OK) {
                    this.props.history.push(`/login`);
                }
                else {
                    this.setState({ error: status });
                }
            }
            else if (this.mounted) {
                if (response.status === SC_CONFLICT) {
                    const errorMessage = i18next.t('matchErrors.cancelMatchFullOrPlayed');
                    this.setState({ errorMessage: errorMessage, executing: false });
                }
                else {
                    this.setState({ error: response.status, executing: false });
                }
            }
        }
        else {
            const newMatch = Utils.removePlayerFromMatch(match, userId)
            if (this.mounted) {
                this.setState({ match: newMatch, executing: false });
            }
        }
    }
    
    deleteMatch = async (e, match) => {
        e.stopPropagation();
        if (this.mounted) {
            this.setState({ executing: true });
        }
        const response = await MatchService.deleteMatch(match.key);
        if (response.status) {
            if (response.status === SC_UNAUTHORIZED) {
                const status = AuthService.internalLogout();
                if (status === SC_OK) {
                    this.props.history.push(`/login`);
                }
                else {
                    this.setState({ error: status });
                }
            }
            else if (this.mounted) {
                if (response.status === SC_CONFLICT) {
                    const errorMessage = i18next.t('matchErrors.cancelMatchFullOrPlayed');
                    this.setState({ errorMessage: errorMessage, executing: false });
                }
                else {
                    this.setState({ error: response.status, executing: false });
                }
            }
        }
        else {
            this.props.history.push('/');
        }
    }

    updateMatchScore = (e, match) => {
        this.props.history.push(`/setMatchScore/${match.creator}/${match.key}`);
    }

    componentDidMount = async () => {
        this.mounted = true;
        const response = await MatchService.getMatchByKey(this.state.matchKey);
        if (this.mounted) {
            if (response.status) {
                this.setState({ status: response.status });
            }
            else if (this.mounted) {
                this.setState({ match: response });
            }
        } 
    }

    render() {
        let { message } = this.props; 
        if (this.state.errorMessage) {
            message = this.state.errorMessage;
        }
        return (
            <MatchPage currentMatch={this.state.match} match={this.state.match}
                        error={this.state.error} isLoading={!this.state.match}
                        message={message} updateMatchScore={this.updateMatchScore} 
                        joinMatch={this.joinMatch} cancelMatch={this.cancelMatch}
                        deleteMatch={this.deleteMatch} anonymous={this.state.anonymous} 
                        isExecuting={this.state.executing} />
        );
    }

    componentWillUnmount = () => {
        this.mounted = false;
    } 
}

MatchPageContainer.propTypes = {
    match: PropTypes.object,
    message: PropTypes.string,
    matchKey: PropTypes.string,
}

export default MatchPageContainer;