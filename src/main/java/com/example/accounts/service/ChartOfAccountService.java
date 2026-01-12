package com.example.accounts.service;

import com.example.accounts.dto.AccountDimensionRequest;
import com.example.accounts.dto.AccountDimensionResponse;
import com.example.accounts.dto.ChartOfAccountRequest;
import com.example.accounts.dto.ChartOfAccountResponse;
import com.example.accounts.dto.ChartOfAccountTreeNode;
import com.example.accounts.entity.AccountDimension;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    public ChartOfAccountResponse createAccount(ChartOfAccountRequest request) {
        if (chartOfAccountRepository.existsByAccountCode(request.getAccountCode())) {
            throw new DuplicateResourceException("Account code already exists: " + request.getAccountCode());
        }

        ChartOfAccount account = ChartOfAccount.builder()
                .accountCode(request.getAccountCode())
                .description(request.getDescription())
                .accountType(request.getAccountType())
                .section(request.getSection())
                .parentGroup(request.getParentGroup())
                .isOffBalance(request.getIsOffBalance())
                .isQuantitative(request.getIsQuantitative())
                .currency(request.getCurrency())
                .isActive(true)
                .details(request.getDetails())
                .build();

        // Add dimensions
        if (request.getDimensions() != null) {
            for (AccountDimensionRequest dimReq : request.getDimensions()) {
                AccountDimension dimension = AccountDimension.builder()
                        .dimensionType(dimReq.getDimensionType())
                        .turnoversOnly(dimReq.getTurnoversOnly())
                        .trackAmount(dimReq.getTrackAmount())
                        .trackQuantitative(dimReq.getTrackQuantitative())
                        .build();
                account.addDimension(dimension);
            }
        }

        ChartOfAccount saved = chartOfAccountRepository.save(account);
        return mapToResponse(saved);
    }

    public ChartOfAccountResponse getAccountById(Long accountId) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return mapToResponse(account);
    }

    public ChartOfAccountResponse getAccountByCode(String accountCode) {
        ChartOfAccount account = chartOfAccountRepository.findByAccountCode(accountCode)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with code: " + accountCode));
        return mapToResponse(account);
    }

    public List<ChartOfAccountResponse> getAccountsBySection(String section) {
        return chartOfAccountRepository.findBySection(section).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChartOfAccountResponse> getAccountsByParentGroup(String parentGroup) {
        return chartOfAccountRepository.findByParentGroup(parentGroup).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChartOfAccountResponse> getAllAccounts() {
        return chartOfAccountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChartOfAccountResponse> getActiveAccounts() {
        return chartOfAccountRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ChartOfAccountResponse updateAccount(Long accountId, ChartOfAccountRequest request) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        // Check if account code is being changed and if it already exists
        if (!account.getAccountCode().equals(request.getAccountCode()) &&
                chartOfAccountRepository.existsByAccountCode(request.getAccountCode())) {
            throw new DuplicateResourceException("Account code already exists: " + request.getAccountCode());
        }

        account.setAccountCode(request.getAccountCode());
        account.setDescription(request.getDescription());
        account.setAccountType(request.getAccountType());
        account.setSection(request.getSection());
        account.setParentGroup(request.getParentGroup());
        account.setIsOffBalance(request.getIsOffBalance());
        account.setIsQuantitative(request.getIsQuantitative());
        account.setCurrency(request.getCurrency());
        account.setDetails(request.getDetails());

        // Update dimensions - clear existing and add new
        account.getDimensions().clear();
        if (request.getDimensions() != null) {
            for (AccountDimensionRequest dimReq : request.getDimensions()) {
                AccountDimension dimension = AccountDimension.builder()
                        .dimensionType(dimReq.getDimensionType())
                        .turnoversOnly(dimReq.getTurnoversOnly())
                        .trackAmount(dimReq.getTrackAmount())
                        .trackQuantitative(dimReq.getTrackQuantitative())
                        .build();
                account.addDimension(dimension);
            }
        }

        ChartOfAccount updated = chartOfAccountRepository.save(account);
        return mapToResponse(updated);
    }

    public ChartOfAccountResponse activateAccount(Long accountId) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setIsActive(true);
        ChartOfAccount updated = chartOfAccountRepository.save(account);
        return mapToResponse(updated);
    }

    public ChartOfAccountResponse deactivateAccount(Long accountId) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setIsActive(false);
        ChartOfAccount updated = chartOfAccountRepository.save(account);
        return mapToResponse(updated);
    }

    public void deleteAccount(Long accountId) {
        if (!chartOfAccountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }
        chartOfAccountRepository.deleteById(accountId);
    }

    public List<ChartOfAccountTreeNode> getAccountsTree(String code, String description, String type, String section) {
        List<ChartOfAccount> allAccounts = chartOfAccountRepository.findAll();

        // Apply filters if provided
        if (code != null && !code.isEmpty()) {
            String searchCode = code.toLowerCase();
            allAccounts = allAccounts.stream()
                    .filter(a -> a.getAccountCode().toLowerCase().contains(searchCode))
                    .collect(Collectors.toList());
        }

        if (description != null && !description.isEmpty()) {
            String searchDesc = description.toLowerCase();
            allAccounts = allAccounts.stream()
                    .filter(a -> a.getDescription().toLowerCase().contains(searchDesc))
                    .collect(Collectors.toList());
        }

        if (type != null && !type.isEmpty()) {
            String searchType = type.toUpperCase();
            allAccounts = allAccounts.stream()
                    .filter(a -> a.getAccountType().name().contains(searchType))
                    .collect(Collectors.toList());
        }

        if (section != null && !section.isEmpty()) {
            String searchSection = section.toLowerCase();
            allAccounts = allAccounts.stream()
                    .filter(a -> a.getSection().toLowerCase().contains(searchSection))
                    .collect(Collectors.toList());
        }

        // Build a map for quick lookup
        java.util.Map<String, ChartOfAccountTreeNode> nodeMap = new java.util.HashMap<>();
        java.util.Map<String, java.util.List<ChartOfAccountTreeNode>> childrenMap = new java.util.HashMap<>();

        // First pass: create all nodes
        for (ChartOfAccount account : allAccounts) {
            ChartOfAccountTreeNode node = mapToTreeNode(account);
            nodeMap.put(account.getDescription(), node);

            // Initialize children list for this node
            childrenMap.put(account.getDescription(), new java.util.ArrayList<>());
        }

        // Second pass: build parent-child relationships
        List<ChartOfAccountTreeNode> rootNodes = new java.util.ArrayList<>();
        for (ChartOfAccount account : allAccounts) {
            ChartOfAccountTreeNode node = nodeMap.get(account.getDescription());

            if (account.getParentGroup() != null && !account.getParentGroup().equals("Accounts Group")) {
                // Find parent node
                ChartOfAccountTreeNode parent = nodeMap.get(account.getParentGroup());
                if (parent != null) {
                    childrenMap.get(account.getParentGroup()).add(node);
                    node.setLevel(calculateLevel(account.getAccountCode()));
                } else {
                    // Parent not found, treat as root
                    rootNodes.add(node);
                    node.setLevel(1);
                }
            } else {
                // Root level account
                rootNodes.add(node);
                node.setLevel(1);
            }
        }

        // Third pass: assign children to nodes and set hasChildren flag
        for (String key : childrenMap.keySet()) {
            ChartOfAccountTreeNode node = nodeMap.get(key);
            if (node != null) {
                List<ChartOfAccountTreeNode> children = childrenMap.get(key);
                node.setChildren(children);
                node.setHasChildren(!children.isEmpty());
            }
        }

        // Sort root nodes by account code
        rootNodes.sort((a, b) -> a.getAccountCode().compareTo(b.getAccountCode()));

        return rootNodes;
    }

    private ChartOfAccountTreeNode mapToTreeNode(ChartOfAccount account) {
        // Check if account has dimensions
        boolean hasDim1 = account.getDimensions().stream().anyMatch(d -> d.getDimensionType() != null);
        boolean hasDim2 = account.getDimensions().size() > 1;
        boolean hasDim3 = account.getDimensions().size() > 2;

        return ChartOfAccountTreeNode.builder()
                .accountId(account.getAccountId())
                .accountCode(account.getAccountCode())
                .description(account.getDescription())
                .accountType(account.getAccountType().name())
                .section(account.getSection())
                .parentGroup(account.getParentGroup())
                .currency(account.getCurrency())
                .isActive(account.getIsActive())
                .isOffBalance(account.getIsOffBalance())
                .isQuantitative(account.getIsQuantitative())
                .hasDimension1(hasDim1)
                .hasDimension2(hasDim2)
                .hasDimension3(hasDim3)
                .hasChildren(false) // Will be set later
                .children(new java.util.ArrayList<>())
                .build();
    }

    private Integer calculateLevel(String accountCode) {
        // Level based on account code structure
        // 1000000 = Level 1
        // 1010000 = Level 2
        // 1010100 = Level 3
        if (accountCode.endsWith("00000"))
            return 1;
        if (accountCode.endsWith("0000"))
            return 2;
        return 3;
    }

    private ChartOfAccountResponse mapToResponse(ChartOfAccount account) {
        List<AccountDimensionResponse> dimensionResponses = account.getDimensions().stream()
                .map(dim -> AccountDimensionResponse.builder()
                        .dimensionId(dim.getDimensionId())
                        .dimensionType(dim.getDimensionType())
                        .turnoversOnly(dim.getTurnoversOnly())
                        .trackAmount(dim.getTrackAmount())
                        .trackQuantitative(dim.getTrackQuantitative())
                        .build())
                .collect(Collectors.toList());

        return ChartOfAccountResponse.builder()
                .accountId(account.getAccountId())
                .accountCode(account.getAccountCode())
                .description(account.getDescription())
                .accountType(account.getAccountType())
                .section(account.getSection())
                .parentGroup(account.getParentGroup())
                .isOffBalance(account.getIsOffBalance())
                .isQuantitative(account.getIsQuantitative())
                .currency(account.getCurrency())
                .isActive(account.getIsActive())
                .details(account.getDetails())
                .dimensions(dimensionResponses)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    /**
     * Get accounts that can be used as dimension values
     * For example: Get all accounts with WAREHOUSE dimension to populate warehouse
     * dropdown
     */
    public List<ChartOfAccountResponse> getAccountsForDimension(String dimensionType) {
        // Get all accounts
        List<ChartOfAccount> allAccounts = chartOfAccountRepository.findAll();

        // Filter accounts that have the specified dimension type
        return allAccounts.stream()
                .filter(account -> account.getDimensions().stream()
                        .anyMatch(dim -> dim.getDimensionType().name().equals(dimensionType)))
                .filter(ChartOfAccount::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
