package com.techworld.accounts.service.Impl;

import com.techworld.accounts.constants.AccountsConstants;
import com.techworld.accounts.dto.AccountsDto;
import com.techworld.accounts.dto.CustomerDto;
import com.techworld.accounts.entity.Accounts;
import com.techworld.accounts.entity.Customer;
import com.techworld.accounts.exception.CustomerAlreadyExistException;
import com.techworld.accounts.exception.ResourceNotFoundException;
import com.techworld.accounts.mapper.AccountsMapper;
import com.techworld.accounts.mapper.CustomerMapper;
import com.techworld.accounts.repository.AccountsRepository;
import com.techworld.accounts.repository.CustomerRepository;
import com.techworld.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AccountsServiceImpl  implements IAccountsService {

    private AccountsRepository accountsRepository;
    public CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        customerRepository.findByMobileNumber(customer.getMobileNumber())
                .ifPresent(existingCustomer -> {
                    throw new CustomerAlreadyExistException("Customer with mobile number " + customer.getMobileNumber() + " already exists.");
                });
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }


    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();

        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccountNumber = (long) (Math.random() * 10000000000L);
        newAccount.setAccountNumber(randomAccountNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer","Mobile Number" , mobileNumber ));

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account","Customer ID" , customer.getCustomerId().toString() ));
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;

        AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "Account Number", accountsDto.getAccountNumber().toString()));
            // Update account details here
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "Customer ID", customerId.toString()));
            // Update customer details here
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer","Mobile Number" , mobileNumber ));

        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
