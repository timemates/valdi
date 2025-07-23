package com.y9vad9.valdi.domain.detekt

import com.y9vad9.valdi.domain.detekt.rule.AggregateCannotContainAggregateRule
import com.y9vad9.valdi.domain.detekt.rule.AggregateConstructorMustBePrivateRule
import com.y9vad9.valdi.domain.detekt.rule.AggregateMustOnlyContainDomainObjectsRule
import com.y9vad9.valdi.domain.detekt.rule.AggregateShouldNotBeDataClassRule
import com.y9vad9.valdi.domain.detekt.rule.EntityConstructorMustBePrivateRule
import com.y9vad9.valdi.domain.detekt.rule.EntityMustHaveIdRule
import com.y9vad9.valdi.domain.detekt.rule.ValueObjectCannotContainEntityOrAggregateRule
import com.y9vad9.valdi.domain.detekt.rule.ValueObjectMustBeImmutableRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

public class DomainModelRuleSetProvider : RuleSetProvider {

    override val ruleSetId: String = "valdi"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            id = ruleSetId,
            rules = listOf(
                AggregateCannotContainAggregateRule(config),
                AggregateConstructorMustBePrivateRule(config),
                AggregateMustOnlyContainDomainObjectsRule(config),
                AggregateShouldNotBeDataClassRule(config),
                EntityConstructorMustBePrivateRule(config),
                EntityMustHaveIdRule(config),
                ValueObjectCannotContainEntityOrAggregateRule(config),
                ValueObjectMustBeImmutableRule(config),
            ),
        )
    }
}
