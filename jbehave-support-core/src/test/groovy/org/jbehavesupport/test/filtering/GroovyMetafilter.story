A story is collection of scenarios for testing filtering by metadata

Narrative:
In order to make sure that metadata filtering works as expected
As a development team
I want to confirm correct functionality

Meta:
    @inherit i1

Scenario: Inheriting metadata from story
Meta:
    @id 001

Given Something nice happens

Scenario: Overriding attributes
Meta:
    @id 002
    @attribute a2

Given Something nice happens

Scenario: Overriding properties
Meta:
    @id 003
    @property p1 p2

Given Something nice happens

Scenario: Having features
Meta:
    @id 004
    @features f1 f2

Given Something nice happens

Scenario: Having other features
Meta:
    @id 005
    @features f2 f3

Given Something nice happens

Scenario: Ignored story
Meta:
    @id 006
    @ignore

Given Something nice happens

Scenario: Overriding inhertited with value
Meta:
    @id 007
    @inherit i2
Given Something nice happens


Scenario: Overriding inhertited without value
Meta:
    @id 008
    @inherit
Given Something nice happens
