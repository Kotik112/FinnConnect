package com.example.exceptions

class RuntimeExceptions(message: String) : RuntimeException(message)

class NothingInsertedException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : RuntimeException(message)