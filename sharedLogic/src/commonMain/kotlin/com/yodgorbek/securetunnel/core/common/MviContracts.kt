package com.yodgorbek.securetunnel.core.common

/**
 * Base interface representing user intentions or system triggers in MVI.
 */
interface MviIntent

/**
 * Base interface representing the immutable UI state in MVI.
 */
interface MviState

/**
 * Base interface representing one-shot side effects (e.g. navigation, toasts, errors) in MVI.
 */
interface MviEffect
